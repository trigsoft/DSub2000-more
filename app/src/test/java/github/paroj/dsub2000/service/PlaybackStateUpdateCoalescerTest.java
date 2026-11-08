package github.paroj.dsub2000.service;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class PlaybackStateUpdateCoalescerTest {
	private final Object lock = new Object();
	private final Queue<Runnable> tasks = new ArrayDeque<>();
	private final List<String> delivered = new ArrayList<>();
	private String state = "IDLE";
	private int index = -1;
	private int size;
	private final PlaybackStateUpdateCoalescer updates = new PlaybackStateUpdateCoalescer(
			lock, tasks::offer, () -> delivered.add(state + ":" + index + ":" + size));

	private void change(String state, int index, int size) {
		synchronized (lock) {
			this.state = state;
			this.index = index;
			this.size = size;
			updates.request();
		}
	}

	@Test
	public void rapidTransitionsAndQueueChangesDeliverOnlyLatestSnapshot() {
		change("PREPARING", 0, 3);
		change("STARTED", 0, 3);
		change("PAUSED", 2, 5);
		assertEquals(1, tasks.size());
		tasks.remove().run();
		assertEquals(List.of("PAUSED:2:5"), delivered);

		change("PAUSED", 1, 4);
		tasks.remove().run();
		change("IDLE", -1, 0);
		tasks.remove().run();
		assertEquals(List.of("PAUSED:2:5", "PAUSED:1:4", "IDLE:-1:0"), delivered);
	}

	@Test
	public void concurrentRequestsUseTheSameStateLock() throws Exception {
		CountDownLatch start = new CountDownLatch(1);
		List<Thread> workers = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			Thread worker = new Thread(() -> {
				try {
					start.await();
					for (int j = 0; j < 100; j++) {
						change("STARTED", j, j + 1);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});
			workers.add(worker);
			worker.start();
		}
		start.countDown();
		for (Thread worker : workers) {
			worker.join();
		}
		change("STOPPED", 3, 7);
		assertEquals(1, tasks.size());
		tasks.remove().run();
		assertEquals(List.of("STOPPED:3:7"), delivered);
	}

	@Test
	public void requestDuringDeliveryIsNotLost() {
		int[] calls = {0};
		PlaybackStateUpdateCoalescer[] reentrant = new PlaybackStateUpdateCoalescer[1];
		reentrant[0] = new PlaybackStateUpdateCoalescer(lock, tasks::offer, () -> {
			if (++calls[0] == 1) {
				reentrant[0].request();
			}
		});
		reentrant[0].request();
		tasks.remove().run();
		assertEquals(1, tasks.size());
		tasks.remove().run();
		assertEquals(2, calls[0]);
	}

	@Test
	public void shutdownDiscardsPendingAndFutureUpdates() {
		change("STARTED", 0, 1);
		updates.stop();
		tasks.remove().run();
		change("STOPPED", -1, 0);
		assertTrue(delivered.isEmpty());
		assertTrue(tasks.isEmpty());
	}

	@Test
	public void rejectedPostCanBeRetried() {
		int[] attempts = {0};
		PlaybackStateUpdateCoalescer retry = new PlaybackStateUpdateCoalescer(lock, task -> {
			if (++attempts[0] == 1) {
				return false;
			}
			return tasks.offer(task);
		}, () -> delivered.add("latest"));
		retry.request();
		retry.request();
		tasks.remove().run();
		assertEquals(List.of("latest"), delivered);
	}
}

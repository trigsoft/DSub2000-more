package github.paroj.dsub2000.service;

import java.util.function.Predicate;

/** Dispatches the latest playback snapshot under the service's state lock. */
final class PlaybackStateUpdateCoalescer implements Runnable {
	private final Object lock;
	private final Predicate<Runnable> post;
	private final Runnable update;
	private boolean pending;
	private boolean stopped;

	PlaybackStateUpdateCoalescer(Object lock, Predicate<Runnable> post, Runnable update) {
		this.lock = lock;
		this.post = post;
		this.update = update;
	}

	void request() {
		synchronized (lock) {
			if (stopped || pending) {
				return;
			}
			pending = true;
			if (!post.test(this)) {
				pending = false;
			}
		}
	}

	@Override
	public void run() {
		synchronized (lock) {
			if (stopped || !pending) {
				return;
			}
			pending = false;
			update.run();
		}
	}

	void stop() {
		synchronized (lock) {
			stopped = true;
			pending = false;
		}
	}
}

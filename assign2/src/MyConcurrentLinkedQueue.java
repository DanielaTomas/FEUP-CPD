import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MyConcurrentLinkedQueue<E> {
    private final LinkedList<E> queue;
    private final Lock lock;

    public MyConcurrentLinkedQueue() {
        queue = new LinkedList<>();
        lock = new ReentrantLock();
    }

    
    public void add(E element) {
        lock.lock();
        try {
            queue.add(element);
        } finally {
            lock.unlock();
        }
    }

    
    public E poll() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    
    public E peek() {
        lock.lock();
        try {
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(E element) {
        lock.lock();
        try {
            return queue.contains(element);
        } finally {
            lock.unlock();
        }
    }

    
    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}

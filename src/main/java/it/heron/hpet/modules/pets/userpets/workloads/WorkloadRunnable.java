package it.heron.hpet.modules.pets.userpets.workloads;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorkloadRunnable implements Runnable {
    private static final double MAX_MILLIS_PER_TICK = 2.5;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    private final Deque<ScheduledWorkload> workloadDeque = new ArrayDeque<>();

    public void addWorkload(ScheduledWorkload workload) {
        this.workloadDeque.add(workload);
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;
        ScheduledWorkload last = this.workloadDeque.peekLast();
        ScheduledWorkload nextLoad = null;

        while (System.nanoTime() <= stopTime && !this.workloadDeque.isEmpty() && nextLoad != last) {
            nextLoad = this.workloadDeque.poll();
            nextLoad.compute();
            if (nextLoad.shouldBeRescheduled()) {
                this.addWorkload(nextLoad);
            }
        }
    }
}


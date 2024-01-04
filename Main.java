import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveTask;

class WaveSumCalculator extends RecursiveTask<Integer> {
    private static final int startLimitForOneThread = 5;
    private int[] array;
    private int start;
    private int end;

    public WaveSumCalculator(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int length = end - start;
        if (length <= startLimitForOneThread) {
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            int middle = start + length / 2;
            WaveSumCalculator leftTask = new WaveSumCalculator(array, start, middle);
            WaveSumCalculator rightTask = new WaveSumCalculator(array, middle, end);

            leftTask.fork();
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();

            return leftResult + rightResult;
        }
    }
}

public class ParallelWaveSum {
    public static void main(String[] args) {
        int arraySize = 100000;
        int[] array = generateRandomArray(arraySize);

        int processors = Runtime.getRuntime().availableProcessors();
        Executor executor = Executors.newFixedThreadPool(processors);

        Runnable runnable = () -> {
            WaveSumCalculator task = new WaveSumCalculator(array, 0, array.length);
            int result = ((RecursiveTask<Integer>) task).fork().join();
            System.out.println("Сума масиву: " + result);
        };

        long startTime = System.currentTimeMillis();

        executor.execute(runnable);

        long endTime = System.currentTimeMillis();

        System.out.println("Час виконання: " + (endTime - startTime) + " мс");
    }

    private static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (int) (Math.random() * 100);
        }
        return array;
    }
}

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class JustApp {

    public static int insideJust(int value) {
        p("inside just("+value+")");
        return value;

    }

    public static void main(String[] args) {

        justInOneThread();
        p("\n\n\n");
        justInMultiThreads();



//        Observable.interval(1, TimeUnit.SECONDS)
//
//                .observeOn(Schedulers.newThread())
//                .doOnNext(param -> p("1: " + threadName()))
//
//                .observeOn(Schedulers.io())
//                .doOnNext(param -> p("2: " + threadName()))
//
//                .subscribe(param -> p("result: " + threadName() + ": " + Long.toString(param)));
//
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void justInOneThread() {
        Observable.just(insideJust(10), insideJust(11), insideJust(12), insideJust(13))
                .doOnNext(value -> p("log1: " + value + ": " + threadName()))

                .doOnNext(value -> p("log2: " + value + ": " + threadName()))

                .doOnNext(value -> p("log3: " + value + ": "  + threadName()))

                .subscribe(value -> p("result: " + Integer.toString(value)));

    }

    private static void justInMultiThreads() {

        Observable.just(insideJust(21), insideJust(22), insideJust(23), insideJust(24))
                .doOnNext(value -> p("log1: " + value + ": " + threadName()))

                .observeOn(Schedulers.newThread())
                .doOnNext(value -> p("log2: " + value + ": " + threadName()))

                .observeOn(Schedulers.io())
                .doOnNext(value -> p("log3: " + value + ": " + threadName()))

                .subscribe(value -> p("result: " + Integer.toString(value)));

    }

    private static void p(String message) {
        System.out.println(message);
    }

    private static String threadName() {
        return Thread.currentThread().getName();
    }
}

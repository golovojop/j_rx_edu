import io.reactivex.Observable;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class DeferApp {

    public static void main(String[] args) {
        System.out.println("App started in thread: " + Thread.currentThread().getName() + "\n\n");

        /**
         * defer - это ПОРОЖДАЮЩИЙ оператор.
         * На входе: берет Callable<Observable<R>>
         * На выходе: выдает инстанс ObservableDefer<R>.
         *
         * Дальше самое интересное. Мы вызываем subscribe() на ObservableDefer<R>, передавая свой
         * Observer<R>, а ObservableDefer<R> внутри себя вызывает наш Callable, который
         * генерит Observable<R> и именно на него подписывает нашего Observer'а. То есть
         * ObservableDefer<R> работает как прокси. Сам ничего не эмиттирует, просто предоставляет
         * возможность "отложенной" подписки на реальный эмитирующий Observable.
         *
         * Как я понял основное отличие механизма defer() от fromCallable в том, что последний
         * может эмитировать только одно значения, возвращаемое из Callable, а defer() может подписать
         * на любой Observable.
         *
         */

        Observable obd = Observable.defer(() ->
           Observable.fromIterable(UserCache.getAllUsers())
        );

        // Распечатать инфу о классах
        exploreObservableDefer(obd);

        // Адский эмиттер
        exploreCustomEmitter();

        Observable.just("H").map(s -> s + ":" + 100);


    }

    // Создаем свой эмиттер и подписываемся
    static void exploreCustomEmitter() {
        Observable<Pair<Integer, String>> obs = Observable.create(emitter -> {
            List<User> li = UserCache.getAllUsers();
            final int[] i = {0};

            new Thread(() -> {
                while(true) {
                    if(i[0] >= li.size()) i[0] = 0;
                    emitter.onNext(new Pair(i[0], li.get(i[0]).name));
                    i[0]++;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });

        obs.subscribe((Pair<Integer, String> integerStringPair) -> {
            System.out.printf("i=%d, string=%s, thread=%s\n",
                    integerStringPair.getKey(),
                    integerStringPair.getValue(),
                    Thread.currentThread().getName());

        });
    }

    // Посмотреть названия классов
    static void exploreObservableDefer(Observable obd) {
        System.out.println(obd.getClass().getCanonicalName());
        obd.subscribe(obj -> {
            System.out.println(obj.getClass().getCanonicalName());
        });
    }
}

class UserCache {
    public static List<User> getAllUsers() {
        return Arrays.asList(
                new User("Ivan", null),
                new User("Olga", "Olga blog"),
                new User("Peter", "Peter blog"));
    }
}

class User {
    String name;
    String blog;

    public User(String name, String blog) {
        this.name = name;
        this.blog = blog;
    }
}

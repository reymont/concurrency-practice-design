package chap6;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hjy on 18-2-23.
 */
public class CompletableFuture3 {

    public static Integer calc(Integer para) {
        return para / 2;
    }

    public static void main(String[] args) throws Exception {
        CompletableFuture<Void> fu = CompletableFuture.supplyAsync(() -> calc(50))
                .thenCompose((i) -> CompletableFuture.supplyAsync(() -> calc(i)))
                .thenApply((str) -> "\"" + str + "\"").thenAccept(System.out::println);
        fu.get();
    }


}

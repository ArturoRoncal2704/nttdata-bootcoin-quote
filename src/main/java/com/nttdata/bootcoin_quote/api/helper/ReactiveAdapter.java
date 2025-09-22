package com.nttdata.bootcoin_quote.api.helper;

import io.reactivex.rxjava3.core.Single;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class ReactiveAdapter {
    
    public <T> Mono<T> singleToMono(Single<T> single) {
        return Mono.fromCompletionStage(single.toCompletionStage());
    }
}
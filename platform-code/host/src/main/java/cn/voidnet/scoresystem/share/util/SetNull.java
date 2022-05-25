package cn.voidnet.scoresystem.share.util;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SetNull<T, R> implements Function<T, T> {
    BiConsumer<T, R> setter;

    public SetNull(BiConsumer<T, R> setter) {
        this.setter = setter;
    }

    //((T,U)->void)->((T)->T)）
    public static <T, R> SetNull<T, R> of(BiConsumer<T, R> setter) {
        return new SetNull<>(setter);
    }


    @Override
    public T apply(T t) {
        setter.accept(t, null);
        return t;
    }
}


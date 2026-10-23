package com.example.aitanskillsdemo.data;

import java.util.Locale;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class TestBackend {
    private final PublishSubject<String> input = PublishSubject.create();

    private final Observable<String> output = input.map(text -> text.toUpperCase(Locale.ROOT));

    public Observable<String> getOutput() {
        return output;
    }

    public void send(String text) {
        input.onNext(text);
    }
}

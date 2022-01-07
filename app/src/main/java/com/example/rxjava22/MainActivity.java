package com.example.rxjava22;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.example.rxjava22.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        //Episode 6 SubscribeOn and ObserveOn
        //new Observable and subscribe
//        Observable.just(1,2,3,4,5)
//                .subscribeOn(Schedulers.io())
//                .doOnNext(s-> Log.d(TAG, "ibrahem UpStream: "+s+Thread.currentThread().getName()))
//                .observeOn(Schedulers.computation())
//                .observeOn(Schedulers.trampoline())
//                .subscribe(o-> Log.d(TAG, "ibrahem DownStream: "+o+Thread.currentThread().getName()));

        //Episode 7 RX Operators Map and FlatMap .....

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                binding.txName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()!=0)
                        emitter.onNext(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        })
                .doOnNext(s -> Log.d(TAG, "UpStream: " + s))
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) throws Throwable {
                        return Integer.parseInt(o.toString())*2;
                    }
                })
                .debounce(2, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .filter(c-> ! c.toString().equals("ibrahem"))
                .subscribe(d -> {
                    Log.d(TAG, "DownStream: " + d);
                    sendDataToAPI(d.toString());
                });


    }

    public Observable sendDataToAPI(String data){
        Observable observable = Observable.just("Calling Api and send"+data);
        observable.subscribe(c-> Log.d(TAG, "sendDataToAPI: "+c));
        return observable;
    }

    public void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
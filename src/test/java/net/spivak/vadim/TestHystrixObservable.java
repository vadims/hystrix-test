package net.spivak.vadim;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.*;
import org.junit.Test;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHystrixObservable {

  public static class DummyCommand extends HystrixCommand<String> {

    public DummyCommand() {
      super(HystrixCommandGroupKey.Factory.asKey("DummyGroup"));
    }

    @Override
    protected String run() throws Exception {
      return "foo";
    }
  }

  @After
  public void tearDown() throws Exception {
    Hystrix.reset();
  }

  @Test(timeout = 5000)
  public void testSimple() throws Exception {
    Observable<String> result = new DummyCommand().toObservable();
    assertThat(result.toBlockingObservable().single()).isEqualTo("foo");
  }

  @Test(timeout = 5000)
  public void testFlatMap_Observe() throws Exception {
    Observable<String> result = new DummyCommand().observe()
        .flatMap(s -> new DummyCommand().observe());
    assertThat(result.toBlockingObservable().single()).isEqualTo("foo");
  }

  @Test(timeout = 5000)
  public void testFlatMap_ToObservable() throws Exception {
    Observable<String> result = new DummyCommand().toObservable()
        .flatMap(s -> new DummyCommand().toObservable());
    assertThat(result.toBlockingObservable().single()).isEqualTo("foo");
  }

}

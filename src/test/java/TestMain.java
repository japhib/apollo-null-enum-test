import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Query;
import com.apollographql.apollo3.mockserver.MockResponse;
import com.apollographql.apollo3.mockserver.MockServer;
import com.apollographql.apollo3.mockserver.MockServerKt;
import com.apollographql.apollo3.runtime.java.ApolloClient;
import com.apollographql.apollo3.rx3.java.Rx3Apollo;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.example.GetThingQuery;
import org.example.type.StatusEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class TestMain {
  MockServer mockServer;
  ApolloClient apolloClient;

  @BeforeEach
  public void before() {
    mockServer = MockServerKt.MockServer();

    String mockServerUrl = (String) mockServer.url(new Continuation<>() {
      @NotNull
      @Override
      public CoroutineContext getContext() {
        return EmptyCoroutineContext.INSTANCE;
      }

      @Override
      public void resumeWith(@NotNull Object o) {
      }
    });

    apolloClient = new ApolloClient.Builder().serverUrl(mockServerUrl).build();
  }

  @NotNull
  public static <D extends Query.Data> ApolloResponse<D> blockingQuery(ApolloClient apolloClient, Query<D> query) {
    return Rx3Apollo.single(apolloClient.query(query), BackpressureStrategy.BUFFER).blockingGet();
  }

  @Test
  public void testNormalQuerySucceeds() {
    mockServer.enqueue(new MockResponse.Builder().body("{\"data\":{}}").build());

    var result = blockingQuery(apolloClient, GetThingQuery.builder().build());

    assertNotNull(result);
  }

  // This test hangs forever!!
  @Test
  @Timeout(value = 30)
  public void testNullEnumHangs() {
    mockServer.enqueue(new MockResponse.Builder().body("{\"data\":{}}").build());

    var result = blockingQuery(apolloClient,
        GetThingQuery.builder()
            // !!! Set the input enum to null !!!
            .something(new StatusEnum(null))
            .build()
    );

    assertNotNull(result);
  }
}

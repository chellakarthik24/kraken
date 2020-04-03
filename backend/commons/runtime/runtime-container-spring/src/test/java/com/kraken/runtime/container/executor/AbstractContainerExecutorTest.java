package com.kraken.runtime.container.executor;

import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.container.properties.ContainerProperties;
import com.kraken.runtime.container.properties.ContainerPropertiesTest;
import com.kraken.runtime.entity.task.FlatContainer;
import com.kraken.runtime.entity.task.FlatContainerTest;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractContainerExecutorTest {

  @Mock
  protected ContainerExecutor containerExecutor;
  @Mock
  protected RuntimeClient runtimeClient;

  protected FlatContainer me;
  protected ContainerProperties containerProperties;

  @Before
  public void setUp() {
    me = FlatContainerTest.CONTAINER;
    containerProperties = ContainerPropertiesTest.RUNTIME_PROPERTIES;
    doAnswer(invocation -> {
      final Optional<Consumer<FlatContainer>> setUp = invocation.getArgument(0);
      final Consumer<FlatContainer> execute = invocation.getArgument(1);
      final Optional<Consumer<FlatContainer>> tearDown = invocation.getArgument(2);
      setUp.ifPresent(consumer -> consumer.accept(me));
      execute.accept(me);
      tearDown.ifPresent(consumer -> consumer.accept(me));
      return null;
    }).when(containerExecutor).execute(any(), any(), any());
  }

}
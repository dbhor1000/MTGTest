package org.example.mtgtests.resources;
import org.opentest4j.AssertionFailedError;

public final class TestResourceConfigurationException extends AssertionFailedError {

  public TestResourceConfigurationException(String message, Throwable thrown) {
    super(message, thrown);
  }
}

package com.darrenswhite.rs.ironquest;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import org.hamcrest.Matcher;
import org.hamcrest.beans.HasPropertyWithValue;

public class Matchers {

  @SuppressWarnings("unchecked")
  public static <T> Matcher<T> hasPropertyAtPath(String path, Matcher<?> valueMatcher) {
    List<String> properties = Arrays.asList(path.split("\\."));
    ListIterator<String> iterator = properties.listIterator(properties.size());

    Matcher<?> ret = valueMatcher;
    while (iterator.hasPrevious()) {
      ret = new HasPropertyWithValue<>(iterator.previous(), ret, "%s.");
    }
    return (Matcher<T>) ret;
  }
}

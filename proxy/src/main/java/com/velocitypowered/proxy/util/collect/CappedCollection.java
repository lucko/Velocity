package com.velocitypowered.proxy.util.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingCollection;

import java.util.Collection;
import java.util.HashSet;

/**
 * An unsynchronized collection that puts an upper bound on the size of the collection.
 */
public class CappedCollection<T> extends ForwardingCollection<T> {

  private final Collection<T> delegate;
  private final int upperSize;

  private CappedCollection(Collection<T> delegate, int upperSize) {
    this.delegate = delegate;
    this.upperSize = upperSize;
  }

  /**
   * Creates a capped collection backed by a {@link HashSet}.
   * @param maxSize the maximum size of the collection
   * @param <T> the type of elements in the collection
   * @return the new collection
   */
  public static <T> Collection<T> newCappedSet(int maxSize) {
    return new CappedCollection<>(new HashSet<>(), maxSize);
  }

  @Override
  protected Collection<T> delegate() {
    return delegate;
  }

  @Override
  public boolean add(T element) {
    if (this.delegate.size() >= upperSize) {
      Preconditions.checkState(this.delegate.contains(element), "collection is too large (%s >= %s)",
          this.delegate.size(), this.upperSize);
      return false;
    }
    return this.delegate.add(element);
  }

  @Override
  public boolean addAll(Collection<? extends T> collection) {
    return this.standardAddAll(collection);
  }
}

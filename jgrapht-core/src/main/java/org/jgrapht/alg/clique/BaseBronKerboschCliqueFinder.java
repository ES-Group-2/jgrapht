/*
 * (C) Copyright 2005-2020, by Ewgenij Proschak and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.alg.clique;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximalCliqueEnumerationAlgorithm;

/**
 * Base implementation of the Bron-Kerbosch algorithm.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Ewgenij Proschak
 */
abstract class BaseBronKerboschCliqueFinder<V, E>
    implements
    MaximalCliqueEnumerationAlgorithm<V, E>
{
    /**
     * The underlying graph
     */
    private final Graph<V, E> graph;
    /**
     * Timeout in nanoseconds
     */
    private final long nanos;
    /**
     * Whether the last computation terminated due to a time limit.
     */
    private boolean timeLimitReached;
    /**
     * The result
     */
    private List<Set<V>> allMaximalCliques;
    /**
     * Size of biggest maximal clique found.
     */
    private int maxSize;

    /**
     * Constructor
     * 
     * @param graph the input graph; must be simple
     * @param timeout the maximum time to wait, if zero no timeout
     * @param unit the time unit of the timeout argument
     */
    public BaseBronKerboschCliqueFinder(Graph<V, E> graph, long timeout, TimeUnit unit)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (timeout == 0L) {
            this.nanos = Long.MAX_VALUE;
        } else {
            this.nanos = unit.toNanos(timeout);
        }
        if (this.nanos < 1L) {
            throw new IllegalArgumentException("Invalid timeout, must be positive");
        }
        this.timeLimitReached = false;
    }

    @Override
    public Iterator<Set<V>> iterator()
    {
        lazyRun();
        return allMaximalCliques.iterator();
    }

    /**
     * Create an iterator which returns only the maximum cliques of a graph. The iterator computes
     * all maximal cliques and then filters them by the size of the maximum found clique.
     * 
     * @return an iterator which returns only the maximum cliques of a graph
     */
    public Iterator<Set<V>> maximumIterator()
    {
        lazyRun();
        return allMaximalCliques.stream().filter(c -> c.size() == maxSize).iterator();
    }

    /**
     * Check the computation has stopped due to a time limit or due to computing all maximal
     * cliques.
     * 
     * @return true if the computation has stopped due to a time limit, false otherwise
     */
    public boolean isTimeLimitReached()
    {
        return timeLimitReached;
    }

    /**
     * Lazily start the computation.
     */
    protected abstract void lazyRun();

	public List<Set<V>> getAllMaximalCliques() {
		return allMaximalCliques;
	}

	public void setAllMaximalCliques(List<Set<V>> allMaximalCliques) {
		this.allMaximalCliques = allMaximalCliques;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	public long getNanos() {
		return nanos;
	}

	public void setTimeLimitReached(boolean timeLimitReached) {
		this.timeLimitReached = timeLimitReached;
	}

}

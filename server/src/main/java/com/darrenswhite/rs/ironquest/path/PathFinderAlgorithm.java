package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Collection;
import java.util.Iterator;

/**
 * Interface used to iterate a {@link Collection} of {@link Quest}s in optimal order.
 *
 * @author Darren S. White
 */
public interface PathFinderAlgorithm extends Iterator<Quest> {

}

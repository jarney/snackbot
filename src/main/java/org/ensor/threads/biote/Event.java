package org.ensor.threads.biote;

import org.ensor.data.atom.Atom;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.Pair;
import org.ensor.data.atom.StringAtom;
import org.json.JSONObject;

/**
 * An Event is a dictionary with a name.  These are primarily used as
 * the primitive event data type.  Data can be placed into the property tree
 * with a given name and simply 'sent' to another Biote
 * through {@link org.ensor.robots.scheduler.Biote#sendStimulus}.
 * An event can be created directly from a JSON Object, from a given dictionary,
 * or can be constructed as an empty dictionary and values can be added after
 * the fact.
 *
 * @author Jon
 */
public class Event {
        private final String         mEventName;
        private final ImmutableDict  mEventData;
        
        public Event(final String eventName) {
            this(eventName, ImmutableDict.newAtom());
        }
        
        public Event(final String eventName, final DictionaryAtom atom) {
            mEventName = eventName;
            mEventData = ImmutableDict.newAtom(new Pair[] {
                new Pair<String, Atom>(
                        "event-name",
                        StringAtom.newAtom(mEventName)),
                new Pair<String, Atom>(
                        "event-data",
                        atom.getImmutable())
            });
        }
        public Event(final String eventName, final ImmutableDict atom) {
            mEventName = eventName;
            mEventData = ImmutableDict.newAtom(new Pair[] {
                new Pair<String, Atom>(
                        "event-name",
                        StringAtom.newAtom(mEventName)),
                new Pair<String, Atom>(
                        "event-data",
                        atom.getImmutable())
            });
        }
        public ImmutableDict getData() {
            return mEventData.getDictionary("event-data");
        }
	public String getEventName() {
            return mEventData.getString("event-name");
	}
};


package org.ensor.robots.scheduler;

import org.ensor.robots.primitives.DictionaryAtom;
import org.ensor.robots.primitives.JSONSerializable;
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
public class Event extends DictionaryAtom implements JSONSerializable {
	private String		mEventName;
        private int             mOriginalRecipient;
        public Event(String eventName) {
            super();
            mEventName = eventName;
            mOriginalRecipient = 0;
	}
        public Event(String eventName, DictionaryAtom atom) {
            super(atom);
            mEventName = eventName;
            mOriginalRecipient = 0;
        }
        public Event(String eventName, JSONObject jso) throws Exception {
            super();
            mEventName = eventName;
            deserializeFromJSON(jso);
            mOriginalRecipient = 0;
        }
        public int getOriginalRecipient() {
            return mOriginalRecipient;
        }
        public void setOriginalRecipient(int originalRecipient) {
            mOriginalRecipient = originalRecipient;
        }
	public String getEventName() {
            return mEventName;
	}
};


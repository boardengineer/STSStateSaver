package savestate;


import com.google.gson.JsonObject;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StateElement {
    String encode();

    JsonObject jsonEncode();

    void restore();

    class ElementFactories {
        public final Supplier<StateElement> factory;
        public final Function<String, StateElement> jsonFactory;
        public Function<JsonObject, StateElement> jsonObjectFactory = null;

        public ElementFactories(Supplier<StateElement> factory, Function<String, StateElement> jsonFactory,Function<JsonObject, StateElement> jsonObjectFactory) {
            this.jsonObjectFactory = jsonObjectFactory;
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }

        public ElementFactories(Supplier<StateElement> factory, Function<String, StateElement> jsonFactory) {
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }
    }
}

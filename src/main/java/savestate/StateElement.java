package savestate;


import java.util.function.Function;
import java.util.function.Supplier;

public interface StateElement {
    String encode();

    void restore();

    class ElementFactories {
        public final Supplier<StateElement> factory;
        public final Function<String, StateElement> jsonFactory;

        public ElementFactories(Supplier<StateElement> factory, Function<String, StateElement> jsonFactory) {
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }
    }
}

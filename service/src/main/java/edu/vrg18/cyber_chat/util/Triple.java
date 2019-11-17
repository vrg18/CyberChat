package edu.vrg18.cyber_chat.util;

import lombok.Value;

@Value(staticConstructor = "of")
public class Triple<V1, V2, V3> {
    V1 value1;
    V2 value2;
    V3 value3;
}

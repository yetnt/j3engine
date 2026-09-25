package com.j3d.engine.react.events.payloads;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.gen.grid.ReactivePoint;

public class ReactivePointInvalidatedPayload extends EventPayload<ReactivePoint> {
    public ReactivePointInvalidatedPayload(ReactivePoint e) {
        super(e);
    }
}

package com.orbitalix.action;

import java.util.Objects;

public record ActionResult(boolean successful, String message) {
    public ActionResult { Objects.requireNonNull(message); }
    public static ActionResult success(String message) { return new ActionResult(true, message); }
    public static ActionResult failure(String message) { return new ActionResult(false, message); }
}

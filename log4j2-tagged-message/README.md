# Simple string key-value message for Log4J2

[![Build Status](https://travis-ci.org/gsson/log4j2-tagged-message.svg)](https://travis-ci.org/gsson/log4j2-tagged-message)

The goal is to provide messages immutable logging tags to enable structured logging while being reasonably performant. Useful in cases where MDC or other thread local contexts are not available or unwieldy, such as certain reactive frameworks.

For the best rendering, use with a layout that supports this message type.


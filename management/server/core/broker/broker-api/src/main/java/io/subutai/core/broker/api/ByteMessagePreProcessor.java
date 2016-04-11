package io.subutai.core.broker.api;


/**
 * Allows clients to intercept incoming messages before they are dispatched to message listeners.
 *
 * Messages can be altered by pre-processors.
 */
public interface ByteMessagePreProcessor extends ByteMessageInterceptor
{

}
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws.handler;

import javax.xml.ws.ProtocolException;
import javax.xml.namespace.QName;

/** The <code>LogicalHandler</code> extends
 *  Handler to provide typesafety for the message context parameter.
 * 
 *  @since JAX-WS 2.0
**/
public interface LogicalHandler<C extends LogicalMessageContext> extends Handler<C> {
}
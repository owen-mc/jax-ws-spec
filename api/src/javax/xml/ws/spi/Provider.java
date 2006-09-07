/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *$Id: Provider.java,v 1.2.2.5 2006-09-07 02:41:03 kohlert Exp $
 */

package javax.xml.ws.spi;

import javax.xml.bind.JAXBContext;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOMFeature;

/**
 * Service provider for <code>ServiceDelegate</code> and
 * <code>Endpoint</code> objects.
 * <p>
 * 
 * @since JAX-WS 2.0
 */
public abstract class Provider {
    
  /**
   * A constant representing the property used to lookup the
   * name of a <code>Provider</code> implementation 
   * class.
   */
  static public final String JAXWSPROVIDER_PROPERTY
        = "javax.xml.ws.spi.Provider";

  /**
   * A constant representing the name of the default 
   * <code>Provider</code> implementation class.
  **/
  static private final String DEFAULT_JAXWSPROVIDER
        = "com.sun.xml.ws.spi.ProviderImpl";
  
  
    /**
     * Creates a new instance of Provider 
     */
    protected Provider() {
    }
    
    /**
     *
     * Creates a new provider object.
     * <p>
     * The algorithm used to locate the provider subclass to use consists
     * of the following steps:
     * <p>
     * <ul>
     * <li>
     *   If a resource with the name of
     *   <code>META-INF/services/javax.xml.ws.spi.Provider</code>
     *   exists, then its first line, if present, is used as the UTF-8 encoded
     *   name of the implementation class.
     * </li>
     * <li>
     *   If the $java.home/lib/jaxws.properties file exists and it is readable by
     *   the <code>java.util.Properties.load(InputStream)</code> method and it contains
     *   an entry whose key is <code>javax.xml.ws.spi.Provider</code>, then the value of
     *   that entry is used as the name of the implementation class.
     * </li>
     * <li>
     *   If a system property with the name <code>javax.xml.ws.spi.Provider</code>
     *   is defined, then its value is used as the name of the implementation class.
     * </li>
     * <li>
     *   Finally, a default implementation class name is used.
     * </li>
     * </ul>
     *
     */
    public static Provider provider() {
        try {
            return (Provider)
            FactoryFinder.find(JAXWSPROVIDER_PROPERTY,
                               DEFAULT_JAXWSPROVIDER);
        } catch (WebServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WebServiceException("Unable to create Provider: "+
                                ex.getMessage());
        }

    }
    
    /**
     * Creates a service delegate object.
     * <p>
     * @param wsdlDocumentLocation A URL pointing to the WSDL document
     *        for the service, or <code>null</code> if there isn't one.
     * @param serviceName The qualified name of the service.
     * @param serviceClass The service class, which must be either
     *        <code>javax.xml.ws.Service</code> or a subclass thereof.
     * @return The newly created service delegate.
     */
    public abstract ServiceDelegate createServiceDelegate(
                                    java.net.URL wsdlDocumentLocation,
                                    QName serviceName, Class serviceClass);
    
        
    /** 
     *
     * Creates an endpoint object with the provided binding and implementation
     * object.
     *
     * @param bindingId A URI specifying the desired binding (e.g. SOAP/HTTP)
     * @param implementor A service implementation object to which
     *        incoming requests will be dispatched. The corresponding
     *        class must be annotated with all the necessary Web service
     *        annotations.
     * @return The newly created endpoint.
     */
    public abstract Endpoint createEndpoint(String bindingId, 
                                            Object implementor);

    /** 
     *
     * Creates an endpoint object with the provided binding, features
     * and implementation object.
     *
     * @param bindingId A URI specifying the desired binding (e.g. SOAP/HTTP)
     * @param featuures An array of Features to enable for the specified
     *        binding
     * @param implementor A service implementation object to which
     *        incoming requests will be dispatched. The corresponding
     *        class must be annotated with all the necessary Web service
     *        annotations.
     * @return The newly created endpoint.
     * @throws WebServiceException If any feature is unsupported or incompatible
     * with the specified bindingId
     *
     * @since JAX-WS 2.1
     */
    public abstract Endpoint createEndpoint(String bindingId, String[] features,
                                            Object implementor);
    
    /**
     * Creates and publishes an endpoint object with the specified
     * address and implementation object.
     *
     * @param address A URI specifying the address and transport/protocol
     *        to use. A http: URI must result in the SOAP 1.1/HTTP
     *        binding being used. Implementations may support other
     *        URI schemes.
     * @param implementor A service implementation object to which
     *        incoming requests will be dispatched. The corresponding
     *        class must be annotated with all the necessary Web service
     *        annotations.
     * @return The newly created endpoint.
     */
    public abstract Endpoint createAndPublishEndpoint(String address,
						      Object implementor);
    /**
     * read an EndpointReference from the infoset contained in
     * <code>eprInfoset</code>.
     * 
     * @returns the <code>EndpointReference</code> unmarshalled from
     * <code>eprInfoset</code>.  This method never returns <code>null</code>.
     * 
     * @throws WebServiceException If there is an error creating the
     * <code>EndpointReference</code> from the specified <code>eprInfoset</code>.
     * 
     * @throws NullPointerException If the <code>null</code> 
     * <code>eprInfoset</code> value is given.
     *
     * @since JAX-WS 2.1
     **/
    public abstract EndpointReference readEndpointReference(javax.xml.transform.Source eprInfoset);    
    
    /** 
     * The getPort method returns a proxy/stub.
     * The parameter  <code>serviceEndpointInterface</code> specifies
     * the service endpoint interface that is supported by the
     * returned proxy.
     * The parameter <code>endpointReference</code> specifies the
     * endpoint that will be invoked by the returned stub.
     * In the implementation of this method, the JAX-WS
     * runtime system takes the responsibility of selecting a protocol
     * binding (and a port) and configuring the proxy accordingly from
     * the WSDL Metadata from the <code>EndpointReference</code>.
     * The returned proxy should not be reconfigured by the client.
     * If this Service instance has a known proxy port that matches
     * the information contained in the <code>EndpointReference</code>
     * then that proxy is returned, otherwise a WebServiceException
     * is thrown.
     *
     * @param endpointReference the EndpointReference that will
     * be invoked by the returned proxy.
     * @param serviceEndpointInterface Service endpoint interface
     * @return Object instance that supports the
     *                  specified service endpoint interface
     * @throws WebServiceException
     *                  <UL>
     *                  <LI>If there is an error during creation
     *                      of the proxy
     *                  <LI>If there is any missing WSDL metadata
     *                      as required by this method such as
     *                      the wsaw:ServiceName/@EndpointName
     *                  <LI>Optionally, if an illegal
     *                      <code>endpointReference</code>
     *                      is specified
     *                  <LI>Optionally, if an illegal
     *                      <code>serviceEndpointInterface</code>
     *                      is specified
     *                  </UL>
     *
     *  @since JAX-WS 2.1
     **/
    public abstract <T> T getPort(EndpointReference endpointReference,
            Class<T> serviceEndpointInterface);    

    /** 
     * Creates a <code>Dispatch</code> instance for use with objects of
     * the users choosing.
     *
     * @param endpointReference  The <code>EndpointReference</code>
     * for the target service endpoint
     * @param type The class of object used to messages or message
     * payloads. Implementations are required to support
     * javax.xml.transform.Source and javax.xml.soap.SOAPMessage.
     * @param mode Controls whether the created dispatch instance is message
     * or payload oriented, i.e. whether the user will work with complete
     * protocol messages or message payloads. E.g. when using the SOAP
     * protocol, this parameter controls whether the user will work with
     * SOAP messages or the contents of a SOAP body. Mode must be MESSAGE
     * when type is SOAPMessage.
     *
     * @return Dispatch instance
     * @throws WebServiceException If any error in the creation of
     *                  the <code>Dispatch</code> object
     * @see javax.xml.transform.Source
     * @see javax.xml.soap.SOAPMessage
     *
     * @since JAX-WS 2.1
     **/
    public abstract <T> Dispatch<T> createDispatch(EndpointReference endpointReference, 
            Class<T> type, Service.Mode mode);    
    
    /** 
     * Creates a <code>Dispatch</code> instance for use with JAXB
     * generated objects.
     *
     * @param endpointReference  The <code>EndpointReference</code>
     * for the target service endpoint
     * @param context The JAXB context used to marshall and unmarshall
     * messages or message payloads.
     * @param mode Controls whether the created dispatch instance is message
     * or payload oriented, i.e. whether the user will work with complete
     * protocol messages or message payloads. E.g. when using the SOAP
     * protocol, this parameter controls whether the user will work with
     * SOAP messages or the contents of a SOAP body.
     *
     * @return Dispatch instance
     * @throws ServiceException If any error in the creation of
     *                   the <code>Dispatch</code> object
     *
     * @see javax.xml.bind.JAXBContext
     *
     * @since JAX-WS 2.1
     **/
    public abstract Dispatch<Object> createDispatch(EndpointReference endpointReference,
            JAXBContext context, Service.Mode mode);
    
    
    /**
     * Updates the status of the @link{AddressingFeature}.  Whenever the developer 
     * modifies the AddressingFeature, this method will be called.  The 
     * JAX-WS runtime then must update the runtime behavior to match the 
     * updated AddressingFeature settings.
     *
     * @since JAX-WS 2.1
     */
    public abstract void setAddressingFeature(AddressingFeature addressingFeature);
    
    /**
     * Updates the status of the @link{MTOMFeature}.  Whenever the developer 
     * modifies the MTOMFeature, this method will be called.  The 
     * JAX-WS runtime then must update the runtime behavior to match the 
     * updated MTOMFeature settings.
     *
     * @since JAX-WS 2.1
     */
    public abstract void setMTOMFeature(MTOMFeature MTOMFeature);
}

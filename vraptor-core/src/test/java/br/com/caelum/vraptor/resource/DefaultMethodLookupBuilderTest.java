/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.VRaptorMockery;

public class DefaultMethodLookupBuilderTest {
    
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        
    }
    
    class MyResource {
        public void notAnnotated() {
        }

        @Path("/myPath")
        public void customizedPath() {
        }
        
        @Path("/*/customPath")
        public void starPath() {
        }
    }
    
    class InheritanceExample extends MyResource {
    }
    
    @Test
    public void canTranslateADefaultResource() throws NoSuchMethodException {
        DefaultMethodLookupBuilder builder = new DefaultMethodLookupBuilder();
        String url = builder.urlFor(MyResource.class, mockery.methodFor(MyResource.class, "notAnnotated").getMethod(), new Object[] {});
        assertThat(url, is(equalTo("/MyResource/notAnnotated")));
    }

    @Test
    public void canTranslateAnnotatedMethod() throws NoSuchMethodException {
        DefaultMethodLookupBuilder builder = new DefaultMethodLookupBuilder();
        String url = builder.urlFor(MyResource.class, mockery.methodFor(MyResource.class, "customizedPath").getMethod(), new Object[] {});
        assertThat(url, is(equalTo("/myPath")));
    }

    @Test
    public void canTranslateAInheritedResourceMethod() throws NoSuchMethodException {
        DefaultMethodLookupBuilder builder = new DefaultMethodLookupBuilder();
        String url = builder.urlFor(InheritanceExample.class, mockery.methodFor(MyResource.class, "notAnnotated").getMethod(), new Object[] {});
        assertThat(url, is(equalTo("/InheritanceExample/notAnnotated")));
    }

    @Test
    public void canTranslateAMethodUsingAsteriskAsAPatternMatcher() throws NoSuchMethodException {
        DefaultMethodLookupBuilder builder = new DefaultMethodLookupBuilder();
        String url = builder.urlFor(MyResource.class, mockery.methodFor(MyResource.class, "starPath").getMethod(), new Object[] {});
        assertThat(url, is(equalTo("//customPath")));
    }

    @Test
    public void usesAsteriskBothWays() throws NoSuchMethodException {
        DefaultMethodLookupBuilder builder = new DefaultMethodLookupBuilder();
        Method method = mockery.methodFor(MyResource.class, "starPath").getMethod();
        String url = builder.urlFor(MyResource.class, method, new Object[] {});
        ResourceAndMethodLookup lookup = builder.lookupFor(mockery.resource(MyResource.class));
        assertThat(lookup.methodFor(url, "POST").getMethod(), is(equalTo(method)));
    }


}

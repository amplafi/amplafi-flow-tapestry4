/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package org.amplafi.flow.web.bindings;

import org.amplafi.flow.launcher.ValueFromBindingProvider;
import org.apache.commons.logging.Log;
import org.apache.hivemind.util.PropertyAdaptor;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.form.IFormComponent;

/**
 * @author patmoore
 *
 */
public class ValueFromBindingProviderImpl implements ValueFromBindingProvider {

    private Log log;

    /**
     *
     * @see org.amplafi.flow.launcher.ValueFromBindingProvider#getValueFromBinding(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getValueFromBinding(Object root, String lookup) {
        if ( lookup.charAt(0) == lookup.charAt(lookup.length()-1) && (lookup.charAt(0) == '\'' || lookup.charAt(0) == '\"')) {
            // return literal string
            return lookup.subSequence(1, lookup.length()-1);
        }
        if ( root != null ) {
            String[]props = lookup.split("\\.");
            for(String prop:props) {
                if ( root == null ) {
                    break;
                }
                PropertyAdaptor pa;
                try {
                    // could also be a calculated value that maybe we are not supposed to parse.
                    pa = PropertyUtils.getPropertyAdaptor(root, prop);
                } catch(org.apache.hivemind.ApplicationRuntimeException e) {
                    return lookup;
                }

                if ( pa == null ) {
                    getLog().warn(root.getClass()+"."+prop+" property in path "+lookup+" does not define a property ");
                    root = null;
                } else if (!pa.isReadable()) {
                    getLog().warn(root.getClass()+"."+prop+" property in path "+lookup+" does not define a readable property ");
                    root = null;
                } else {
                    root = pa.read(root);
                }
            }
            if ( root instanceof IFormComponent) {
                IFormComponent component = (IFormComponent) root;
                IBinding binding = component.getBinding("value");
                if (binding== null ) {
                    getLog().debug(lookup + ": component does not have a value binding");
                } else {
                    root = binding.getObject();
                }
            }
        }
        return root;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return the log
     */
    public Log getLog() {
        return log;
    }

}

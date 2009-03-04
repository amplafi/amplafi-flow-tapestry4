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
package org.amplafi.flow.web.models;

import java.util.Collection;
import java.util.Map;

import org.apache.hivemind.Messages;
import org.apache.tapestry.form.BeanPropertySelectionModel;

import static org.apache.commons.lang.StringUtils.*;

/**
 * This class adds localization capabilities to BeanPropertySelectionModel
 *
 * @author shiva
 *
 */
@SuppressWarnings("unchecked")
public class LocalizedBeanPropertySelectionModel extends BeanPropertySelectionModel {

    private static final String MESSAGE_PREFIX = "message:";
    private Messages messages;

    /**
     * Build a amplafi specific localized bean property selection model.
     * @param map {@link Map#values()} will be used as the list of options.
     *
     * @param labelField The label field
     * @param messages
     */

    public LocalizedBeanPropertySelectionModel(Map map, String labelField, Messages messages) {
        super(map.values(), labelField);
        this.messages = messages;
    }

    /**
     * Build a amplafi specific localized bean property selection model.
     *
     * @param c Collection
     * @param labelField The label field
     * @param messages
     */
    public LocalizedBeanPropertySelectionModel(Collection c, String labelField, Messages messages) {
        super(c, labelField);
        this.messages = messages;
    }

    /**
     * @return If the label key starts with message:, then corresponding label is returned from resources
     * else we return the key itself
     */

    @Override
    public String getLabel(int index) {
        String key = super.getLabel(index);
        if ( isBlank(key)) {
            return "[label_"+index+"]";
        } else if (key.startsWith(MESSAGE_PREFIX)) {
            key = messages.getMessage(key.substring(MESSAGE_PREFIX.length()));
        }
        return key;
    }
}

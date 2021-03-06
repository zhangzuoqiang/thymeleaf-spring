/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.spring4.processor;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/**
 * Works in a similar way to <b>#fields.errors()</b>, but lists all errors for
 * the given field name, separated by a &lt;br/&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public final class SpringErrorsTagProcessor extends AbstractAttributeTagProcessor {

    private static final String ERROR_DELIMITER = "<br />";
    
    public static final int ATTR_PRECEDENCE = 1200;
    public static final String ATTR_NAME = "errors";

    

    
    
    public SpringErrorsTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, ATTR_PRECEDENCE);
    }




    @Override
    protected void doProcess(
            final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {

        final BindStatus bindStatus = FieldUtils.getBindStatus(processingContext, attributeValue);

        if (bindStatus.isError()) {

            final StringBuilder strBuilder = new StringBuilder();
            final String[] errorMsgs = bindStatus.getErrorMessages();

            for (int i = 0; i < errorMsgs.length; i++) {
                if (i > 0) {
                    strBuilder.append(ERROR_DELIMITER);
                }
                final String displayString = ValueFormatterWrapper.getDisplayString(errorMsgs[i], false);
                strBuilder.append(HtmlEscape.escapeHtml4Xml(displayString));
            }

            structureHandler.setBody(strBuilder.toString(), false);

            // Just in case we also have a th:errorclass in this tag
            structureHandler.setLocalVariable(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);

            tag.getAttributes().removeAttribute(attributeName);

        } else {

            structureHandler.removeElement();

        }

    }


}

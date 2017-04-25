package org.codehaus.plexus.component.repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 1.0
 */
public class ComponentRequirementList
    extends ComponentRequirement 
{
    private List<String> roleHints;

    public List<String> getRoleHints()
    {
        return roleHints;
    }

    public void setRoleHints(List<String> roleHints)
    {
        this.roleHints = roleHints;
    }

    public String getRoleHint()
    {
        StringBuilder buffer = new StringBuilder();
        for ( String hint : roleHints )
        {
            if (buffer.length() > 0)
            {
                buffer.append(",");
            }

            buffer.append(hint);

        }

        return buffer.toString();
    }
}

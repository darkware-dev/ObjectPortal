/*----------------------------------------------------------------------------------------------
 Copyright (c) 2016. darkware.org and contributors

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ---------------------------------------------------------------------------------------------*/

package org.darkware.objportal;

/**
 * A {@code SimpleContextToken} is a {@link PortalContextToken} with a free-form token
 * key allowing code to freely create tokens at will, managing uniqueness and sharing
 * for itself.
 * <p>
 * It is important to note that the freedom in creating token keys is not absolute. All
 * tokens are prefixed to virtually eliminate the chance of picking a token key that will
 * match against other types of tokens.
 *
 * @author jeff@darkware.org
 * @since 2016-06-15
 */
public class SimpleContextToken extends DefaultContextToken
{
    protected static final String PREFIX = "*";
    private final String keyValue;

    /**
     * Creates a new token with the declared key value. This token is guaranteed to match other
     * {@code SimpleContextToken}s, but there is no guarantee that a specifically crafted key
     * will allow it to match tokens of other types.
     *
     * @param keyValue The key value to use.
     */
    public SimpleContextToken(final String keyValue)
    {
        super();

        this.keyValue = keyValue;
    }

    @Override
    protected String generateKey()
    {
        return SimpleContextToken.PREFIX + this.keyValue;
    }
}

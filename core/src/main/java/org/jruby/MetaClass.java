/***** BEGIN LICENSE BLOCK *****
 * Version: EPL 2.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/epl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the EPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the EPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby;

import org.jruby.runtime.builtin.IRubyObject;

public final class MetaClass extends RubyClass {
    /** rb_class_boot (for MetaClasses) (in makeMetaClass(RubyClass))
     * 
     */
    public MetaClass(Ruby runtime, RubyClass superClass, IRubyObject attached) {
        super(runtime, superClass, false);
        this.attached = attached;
        setClassIndex(superClass.getClassIndex()); // use same ClassIndex as metaclass, since we're technically still of that type
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public final IRubyObject allocate(){
        throw runtime.newTypeError("can't create instance of virtual class");
    }

    public IRubyObject getAttached() {
        return attached;
    }

    public void setAttached(IRubyObject attached) {
        this.attached = attached;
    }

    private IRubyObject attached = null;
}

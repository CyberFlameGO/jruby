/*
 ***** BEGIN LICENSE BLOCK *****
 * Version: EPL 2.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/epl-v20.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
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

package org.jruby.ast;

import java.util.List;

import org.jruby.ParseResult;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.DynamicScope;

/**
 * Represents the top of the AST.  This is a node not present in MRI.  It was created to
 * hold the top-most static scope in an easy to grab way and it also exists to hold BEGIN
 * and END nodes.  These can then be interpreted/compiled in the same places as the rest
 * of the code. 
 *
 */
// TODO: Store BEGIN and END information into this node
public class RootNode extends Node implements ParseResult {
    private transient DynamicScope scope;
    private StaticScope staticScope;
    private Node bodyNode;
    private String file;
    private boolean needsCodeCoverage;

    public RootNode(int line, DynamicScope scope, Node bodyNode, String file) {
        this(line, scope, bodyNode, file, false);
    }

    public RootNode(int line, DynamicScope scope, Node bodyNode, String file, boolean needsCodeCoverage) {
        super(line, bodyNode.containsVariableAssignment());
        
        this.scope = scope;
        this.staticScope = scope.getStaticScope();
        this.bodyNode = bodyNode;
        this.file = file;
        this.needsCodeCoverage = needsCodeCoverage;
    }

    public NodeType getNodeType() {
        return NodeType.ROOTNODE;
    }
    
    /**
     * Return the dynamic scope for this AST.  The variable backed by this is transient so
     * for serialization this is null.  In that case we use staticScope to rebuild the dynamic
     * scope.  The real reason for this method is supporting bindings+eval.  We need to pass
     * our live dynamic scope in so when we eval we can use that dynamic scope. 
     * 
     * @return dynamic scope of this AST
     */
    public DynamicScope getScope() {
        return scope;
    }
    
    /**
     * The static scoping relationships that should get set first thing before interpretation
     * of the code represented by this AST.  Actually, we use getScope first since that also
     * can contain a live dynamic scope.  We rely on this method only for interpreting a root
     * node from a serialized format.
     * 
     * @return the top static scope for the AST
     */
    public StaticScope getStaticScope() {
        return staticScope;
    }

    public String getFile() {
        return file;
    }
    
    /**
     * First real AST node to be interpreted
     * 
     * @return real top AST node
     */
    public Node getBodyNode() {
        return bodyNode;
    }

    public <T> T accept(NodeVisitor<T> iVisitor) {
        return iVisitor.visitRootNode(this);
    }

    public List<Node> childNodes() {
        return createList(bodyNode);
    }

    // Is coverage enabled and is this a valid source file for coverage to apply?
    public boolean needsCoverage() {
        return needsCodeCoverage;
    }
}

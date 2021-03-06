package graphql.nadel.dsl;

import graphql.Internal;
import graphql.language.AbstractNode;
import graphql.language.Comment;
import graphql.language.IgnoredChars;
import graphql.language.Node;
import graphql.language.NodeChildrenContainer;
import graphql.language.NodeVisitor;
import graphql.language.SourceLocation;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Internal
public class UnderlyingServiceHydration extends AbstractNode<UnderlyingServiceHydration> {

    private final String serviceName;
    private final String topLevelField;
    private final List<RemoteArgumentDefinition> arguments;
    private String objectIdentifier;
    private Integer batchSize;

    public UnderlyingServiceHydration(SourceLocation sourceLocation,
                                      List<Comment> comments,
                                      String serviceName,
                                      String topLevelField,
                                      List<RemoteArgumentDefinition> arguments,
                                      String objectIdentifier,
                                      Integer batchSize,
                                      Map<String, String> additionalData
    ) {
        super(sourceLocation, comments, IgnoredChars.EMPTY, additionalData);
        this.serviceName = serviceName;
        this.topLevelField = topLevelField;
        this.arguments = arguments;
        this.objectIdentifier = objectIdentifier;
        this.batchSize = batchSize;
    }


    public Integer getBatchSize() {
        return batchSize;
    }

    public String getObjectIdentifier() {
        return objectIdentifier;
    }


    public String getServiceName() {
        return serviceName;
    }

    public String getTopLevelField() {
        return topLevelField;
    }

    public List<RemoteArgumentDefinition> getArguments() {
        return new ArrayList<>(arguments);
    }

    @Override
    public List<Node> getChildren() {
        return null;
    }

    @Override
    public NodeChildrenContainer getNamedChildren() {
        return null;
    }

    @Override
    public UnderlyingServiceHydration withNewChildren(NodeChildrenContainer newChildren) {
        return null;
    }

    @Override
    public boolean isEqualTo(Node node) {
        return false;
    }

    @Override
    public UnderlyingServiceHydration deepCopy() {
        return null;
    }


    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        return null;
    }
}

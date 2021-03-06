package graphql.nadel.engine

import graphql.ErrorType
import graphql.GraphQLError
import graphql.nadel.StrategyTestHelper
import graphql.nadel.testutils.TestUtil


class NadelExecutionStrategyTest2 extends StrategyTestHelper {

    def "underlying service returns null for non-nullable field"() {
        given:
        def overallSchema = TestUtil.schemaFromNdsl('''
        service Issues {
            type Query {
                issue: Issue
            }
            type Issue {
                id: ID!
            }
        }
        ''')
        def issueSchema = TestUtil.schema("""
            type Query {
                issue: Issue
            }
            type Issue {
                id: ID!
            }
        """)
        def query = "{issue {id}}"

        def expectedQuery1 = "query nadel_2_Issues {issue {id}}"
        def response1 = [issue: [id: null]]

        def overallResponse = [issue: null]


        Map response
        List<GraphQLError> errors
        when:
        (response, errors) = test1Service(
                overallSchema,
                "Issues",
                issueSchema,
                query,
                ["issue"],
                expectedQuery1,
                response1,
        )
        then:
        response == overallResponse
        errors.size() == 1
        errors[0].errorType == ErrorType.NullValueInNonNullableField

    }

    def "non-nullable field error bubbles up"() {
        given:
        def overallSchema = TestUtil.schemaFromNdsl('''
        service Issues {
            type Query {
                issue: Issue!
            }
            type Issue {
                id: ID!
            }
        }
        ''')
        def issueSchema = TestUtil.schema("""
            type Query {
                issue: Issue!
            }
            type Issue {
                id: ID!
            }
        """)
        def query = "{issue {id}}"

        def expectedQuery1 = "query nadel_2_Issues {issue {id}}"
        def response1 = [issue: [id: null]]

        def overallResponse = null


        Map response
        List<GraphQLError> errors
        when:
        (response, errors) = test1Service(
                overallSchema,
                "Issues",
                issueSchema,
                query,
                ["issue"],
                expectedQuery1,
                response1,
        )
        then:
        response == overallResponse
        errors.size() == 1
        errors[0].errorType == ErrorType.NullValueInNonNullableField


    }

    def "non-nullable field error in lists bubbles up to the top"() {
        given:
        def overallSchema = TestUtil.schemaFromNdsl('''
        service Issues {
            type Query {
                issues: [Issue!]!
            }
            type Issue {
                id: ID!
            }
        }
        ''')
        def issueSchema = TestUtil.schema("""
            type Query {
                issues: [Issue!]!
            }
            type Issue {
                id: ID!
            }
        """)
        def query = "{issues {id}}"

        def expectedQuery1 = "query nadel_2_Issues {issues {id}}"
        def response1 = [issues: [[id: null]]]

        def overallResponse = null


        Map response
        List<GraphQLError> errors
        when:
        (response, errors) = test1Service(
                overallSchema,
                "Issues",
                issueSchema,
                query,
                ["issues"],
                expectedQuery1,
                response1,
        )
        then:
        response == overallResponse
        errors.size() == 1
        errors[0].errorType == ErrorType.NullValueInNonNullableField
    }

    def "non-nullable field error in lists bubbles up"() {
        given:
        def overallSchema = TestUtil.schemaFromNdsl('''
        service Issues {
            type Query {
                issues: [[[Issue!]!]]
            }
            type Issue {
                id: ID!
            }
        }
        ''')
        def issueSchema = TestUtil.schema("""
            type Query {
                issues: [[[Issue!]!]]
            }
            type Issue {
                id: ID!
            }
        """)
        def query = "{issues {id}}"

        def expectedQuery1 = "query nadel_2_Issues {issues {id}}"
        def response1 = [issues: [[[[id: null], [id: "will be discarded"]]]]]

        def overallResponse = [issues: [null]]


        Map response
        List<GraphQLError> errors
        when:
        (response, errors) = test1Service(
                overallSchema,
                "Issues",
                issueSchema,
                query,
                ["issues"],
                expectedQuery1,
                response1,
        )
        then:
        response == overallResponse
        errors.size() == 1
        errors[0].errorType == ErrorType.NullValueInNonNullableField

    }

    def "a lot of renames"() {
        given:
        def overallSchema = TestUtil.schemaFromNdsl('''
        service Boards {
            type Query {
                boardScope: BoardScope
            }
            type BoardScope {
                 cardParents: [CardParent]! => renamed from issueParents
            }
            type CardParent => renamed from IssueParent {
                 cardType: CardType! => renamed from issueType
            }
             type CardType => renamed from IssueType {
                id: ID
                inlineCardCreate: InlineCardCreateConfig => renamed from inlineIssueCreate
            }
            
            type InlineCardCreateConfig => renamed from InlineIssueCreateConfig {
                enabled: Boolean!
            }
        }
        ''')
        def boardSchema = TestUtil.schema("""
            type Query {
                boardScope: BoardScope
            }
            type BoardScope {
                issueParents: [IssueParent]!
            }
            type IssueParent {
                issueType: IssueType!
            }
            type IssueType {
                id: ID
                inlineIssueCreate: InlineIssueCreateConfig
            }
            type InlineIssueCreateConfig {
                enabled: Boolean!
            }
        """)
        def query = "{boardScope{ cardParents { cardType {id inlineCardCreate {enabled}}}}}"

        def expectedQuery1 = "query nadel_2_Boards {boardScope {issueParents {issueType {id inlineIssueCreate {enabled}}}}}"
        def response1 = [boardScope: [issueParents: [
                [issueType: [id: "ID-1", inlineIssueCreate: [enabled: true]]]
        ]]]

        def overallResponse = [boardScope: [cardParents: [
                [cardType: [id: "ID-1", inlineCardCreate: [enabled: true]]]
        ]]]


        Map response
        List<GraphQLError> errors
        when:
        (response, errors) = test1Service(
                overallSchema,
                "Boards",
                boardSchema,
                query,
                ["boardScope"],
                expectedQuery1,
                response1,
        )
        then:
        errors.size() == 0
        response == overallResponse

    }

    def "fragment referenced twice from inside Query and inside another Fragment"() {
        given:
        def overallSchema = TestUtil.schemaFromNdsl('''
        service Foo {
              type Query {
                foo: Bar 
              } 
              type Bar {
                 id: String
              }
        }
        ''')
        def underlyingSchema = TestUtil.schema("""
              type Query {
                foo: Bar 
              } 
              type Bar {
                id: String
              }
        """)
        def query = """{foo {id ...F2 ...F1}} fragment F2 on Bar {id} fragment F1 on Bar {id ...F2} """

        def expectedQuery1 = "query nadel_2_Foo {foo {id ...F2 ...F1}} fragment F2 on Bar {id} fragment F1 on Bar {id ...F2}"
        def response1 = [foo: [id: "ID"]]
        def overallResponse = response1


        Map response
        List<GraphQLError> errors
        when:
        (response, errors) = test1Service(
                overallSchema,
                "Foo",
                underlyingSchema,
                query,
                ["foo"],
                expectedQuery1,
                response1,
        )
        then:
        errors.size() == 0
        response == overallResponse


    }
}


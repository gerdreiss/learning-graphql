const graphql = require('graphql');
const _ = require('lodash');
const { GraphQLObjectType, GraphQLString, GraphQLInt, GraphQLSchema } = graphql;

const users = [
  { id: '1', firstName: 'John', lastName: 'Doe', age: 32 },
  { id: '2', firstName: 'Jamie', lastName: 'Doe', age: 32 },
];

const UserType = new GraphQLObjectType({
  name: 'User',
  fields: {
    id: { type: GraphQLString },
    firstName: { type: GraphQLString },
    lastName: { type: GraphQLString },
    age: { type: GraphQLInt },
  },
});

const RootQuery = new GraphQLObjectType({
  name: 'RootQueryType',
  fields: {
    user: {
      type: UserType,
      args: { id: { type: GraphQLString } },
      resolve(parentValue, args) {
        return _.find(users, { id: args.id });
      },
    },
  },
});

module.exports = new GraphQLSchema({ query: RootQuery });

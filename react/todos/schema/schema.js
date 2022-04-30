const axios = require('axios');
const GraphQL = require('graphql');
const GraphQLScalars = require('graphql-scalars');
const { GraphQLObjectType, GraphQLSchema, GraphQLString, GraphQLInt, GraphQLBoolean } = GraphQL;
const { GraphQLEmailAddress } = GraphQLScalars;

const CompanyType = new GraphQLObjectType({
  name: 'Company',
  fields: {
    name: { type: GraphQLString },
    catchPhrase: { type: GraphQLString },
    bs: { type: GraphQLString },
  },
});

const GeoType = new GraphQLObjectType({
  name: 'Geo',
  fields: {
    lat: { type: GraphQLString },
    lng: { type: GraphQLString },
  },
});

const AddressType = new GraphQLObjectType({
  name: 'Address',
  fields: {
    street: { type: GraphQLString },
    suite: { type: GraphQLString },
    city: { type: GraphQLString },
    zipcode: { type: GraphQLString },
    geo: { type: GeoType },
  },
});

const UserType = new GraphQLObjectType({
  name: 'User',
  fields: {
    id: { type: GraphQLInt },
    name: { type: GraphQLString },
    username: { type: GraphQLString },
    email: { type: GraphQLEmailAddress },
    address: { type: AddressType },
    phone: { type: GraphQLString },
    website: { type: GraphQLString },
    company: { type: CompanyType },
  },
});

const TodoType = new GraphQLObjectType({
  name: 'Todo',
  fields: {
    id: { type: GraphQLInt },
    title: { type: GraphQLString },
    completed: { type: GraphQLBoolean },
    user: {
      type: UserType,
      resolve(parentValue, args) {
        return axios
          .get(`https://jsonplaceholder.typicode.com/users/${parentValue.userId}`)
          .then((res) => res.data);
      },
    },
  },
});

const RootQuery = new GraphQLObjectType({
  name: 'RootQueryType',
  fields: {
    todo: {
      type: TodoType,
      args: { id: { type: GraphQLInt } },
      resolve(parentValue, args) {
        return axios
          .get(`https://jsonplaceholder.typicode.com/todos/${args.id}`)
          .then((res) => res.data);
      },
    },
    user: {
      type: UserType,
      args: { id: { type: GraphQLInt } },
      resolve(parentValue, args) {
        return axios
          .get(`https://jsonplaceholder.typicode.com/users/${args.id}`)
          .then((res) => res.data);
      },
    },
  },
});

module.exports = new GraphQLSchema({ query: RootQuery });

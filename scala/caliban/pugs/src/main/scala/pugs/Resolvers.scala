package pugs

val pugService = PugService.make

val queries = Queries(
  args => pugService.findPug(args.name),
  pugService.randomPugPicture
)

val mutations = Mutations(
  args => pugService.addPug(args.pug),
  args => pugService.editPugPicture(args.name, args.pictureUrl)
)

import {
  Box,
  Button,
  Center,
  Flex,
  HStack,
  Image,
  Skeleton,
  SkeletonCircle,
  Stack,
  useColorModeValue,
} from "@chakra-ui/react";

const UserSkeleton = () => {
  return (
    <Center py={6}>
      <Box
        maxW={"270px"}
        w={"full"}
        bg={useColorModeValue("white", "gray.800")}
        boxShadow={"2xl"}
        rounded={"md"}
        overflow={"hidden"}
      >
        <Image
          h={"120px"}
          w={"full"}
          src={useColorModeValue(
            "https://images.unsplash.com/photo-1659276998726-2da312b2e948?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&fit=crop&w=634&q=80",
            "https://images.unsplash.com/photo-1544198365-f5d60b6d8190?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&fit=crop&w=634&q=80",
          )}
          objectFit="cover"
          alt="#"
        />
        <Flex justify={"center"} mt={-12}>
          <SkeletonCircle size="100" />
        </Flex>

        <Box p={6}>
          <Stack spacing={3} align={"center"} mb={5}>
            <Skeleton
              height="15px"
              width="200px"
              bg="green.500"
              color="white"
              fadeDuration={1}
            />
            <Skeleton
              height="10px"
              width="200px"
              bg="green.500"
              color="white"
              fadeDuration={1}
            />
          </Stack>
          <HStack>
            <Button
              w={"full"}
              mt={8}
              bg={useColorModeValue("#151f21", "gray.900")}
              color={"white"}
              rounded={"md"}
              isLoading
            >
              Edit
            </Button>{" "}
            <Button
              w={"full"}
              mt={8}
              bg={useColorModeValue("#151f21", "gray.900")}
              color={"white"}
              rounded={"md"}
              isLoading
            >
              Delete
            </Button>
          </HStack>
        </Box>
      </Box>
    </Center>
  );
};
export default UserSkeleton;

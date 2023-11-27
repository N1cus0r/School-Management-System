import {Box, Flex, Heading, HStack, Skeleton, SkeletonCircle,} from "@chakra-ui/react";

const NavBarSkeleton = () => {
  return (
      <Box px={4}>
          <Flex h={16} alignItems="center" justifyContent="space-between">
              <HStack spacing={8} alignItems="center">
                  <Heading size="md">Geeked-In</Heading>
                  <HStack as="nav" spacing={4} display={{ base: "none", md: "flex" }}>
                      {/*rendered dynamic based on role*/}
                      <Skeleton height='20px'/>
                  </HStack>
              </HStack>
              <HStack alignItems="center">
                 <Skeleton width="150px" height="15px"/>
                  <SkeletonCircle size="10"/>
              </HStack>
          </Flex>
      </Box>
  );
};
export default NavBarSkeleton;

import { Box, Heading, Text } from "@chakra-ui/react";
import { InfoIcon } from "@chakra-ui/icons";
const ContentPlaceholder = () => {
  return (
    <Box textAlign="center" py={10} px={6}>
      <InfoIcon boxSize={"50px"} color={"blue.500"} />
      <Heading as="h2" size="xl" mt={6} mb={2}>
        No content found
      </Heading>
      <Text color={"gray.500"}>It seems there's nothing to display here.</Text>
    </Box>
  );
};
export default ContentPlaceholder;

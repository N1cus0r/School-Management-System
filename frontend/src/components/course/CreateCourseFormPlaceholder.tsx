import {Box, Heading, Text} from "@chakra-ui/react";
import {InfoIcon} from "@chakra-ui/icons";

const CreateCourseFormPlaceholder = () => {
    return (
        <Box textAlign="center" py={10} px={6}>
            <InfoIcon boxSize={"50px"} color={"blue.500"} />
            <Heading as="h2" size="xl" mt={6} mb={2}>
                No teachers found
            </Heading>
            <Text color={"gray.500"}>
                You can try again later when a teacher will be created
            </Text>
        </Box>
    )
}
export default CreateCourseFormPlaceholder

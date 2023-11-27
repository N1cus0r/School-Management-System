import {InfoIcon} from "@chakra-ui/icons";
import {Box, Heading, Text} from "@chakra-ui/react";

const CourseEntityFormPlaceholder = () => {
    return (
        <Box textAlign="center" py={10} px={6}>
            <InfoIcon boxSize={"50px"} color={"blue.500"} />
            <Heading as="h2" size="xl" mt={6} mb={2}>
                No students found
            </Heading>
            <Text color={"gray.500"}>
                You can try again later when a students will be added to this course
            </Text>
        </Box>
    )
}
export default CourseEntityFormPlaceholder

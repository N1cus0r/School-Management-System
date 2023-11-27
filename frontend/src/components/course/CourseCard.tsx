import { Course } from "../../entities/Course.ts";
import {
  Box,
  Button,
  Center,
  Heading,
  HStack,
  Image,
  Stack,
  Text,
  useColorModeValue,
  VStack,
} from "@chakra-ui/react";
import DeleteCourseButton from "./DeleteCourseButton.tsx";
import UpdateCourseButton from "./UpdateCourseButton.tsx";
import { useNavigate } from "react-router-dom";
import AddStudentToCourseButton from "./AddStudentToCourseButton.tsx";
import RemoveStudentFormCourseButton from "./RemoveStudentFormCourseButton.tsx";

interface Props {
  course: Course;
}

const CourseCard = ({ course }: Props) => {
  const navigate = useNavigate();

  return (
    <Center py={6}>
      <Box
        maxW={"270px"}
        w={"full"}
        bg={useColorModeValue("white", "gray.800")}
        boxShadow={"2xl"}
        rounded={"md"}
        overflow={"hidden"}
        _hover={{
          transform: "scale(1.03)",
          transition: "transform .15s ease-in",
        }}
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
        <Box p={6}>
          <Stack spacing={1} align={"center"}>
            <Heading
              fontSize={"xl"}
              fontWeight={500}
              fontFamily={"body"}
              textAlign={"center"}
              onClick={() => navigate(`/courses/${course.id}/homeworks`)}
              _hover={{
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              {course.name}
            </Heading>
            <Text color={"gray.500"}>Teacher: {course.teacherName}</Text>
          </Stack>
          <VStack align={"stretch"}>
            <HStack>
              <UpdateCourseButton course={course} />
              <DeleteCourseButton course={course} />
            </HStack>
            <AddStudentToCourseButton course={course} />
            <RemoveStudentFormCourseButton course={course} />
          </VStack>
        </Box>
      </Box>
    </Center>
  );
};
export default CourseCard;

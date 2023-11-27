import {Grade} from "../../entities/Grade.ts";
import {Heading, HStack, Stack, Text} from "@chakra-ui/react";
import CardContainer from "../shared/CardContainer.tsx";
import DeleteGradeButton from "./DeleteGradeButton.tsx";
import UpdateGradeButton from "./UpdateGradeButton.tsx";

interface Props {
  grade: Grade;
}
const GradeCard = ({ grade }: Props) => {
  // MAYBE GET STUDENT PROFILE PIC AND DISPLAY IT ???
  return (
    <CardContainer>
      <Stack spacing={2} align={"center"} textAlign={"center"}>
        <Heading>{grade.value}</Heading>
        <Heading size={"md"}>{grade.studentName}</Heading>
        <Text>{grade.text}</Text>
        <Text color={"gray.500"}>Published {grade.datePublished}</Text>
      </Stack>
      <HStack>
        <UpdateGradeButton grade={grade} />
        <DeleteGradeButton grade={grade} />
      </HStack>
    </CardContainer>
  );
};
export default GradeCard;

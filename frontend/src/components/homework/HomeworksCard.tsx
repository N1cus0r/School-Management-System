import {Homework} from "../../entities/Homework.ts";
import {HStack, Stack, Text,} from "@chakra-ui/react";
import TextModal from "../shared/TextModal.tsx";
import DeleteHomeworkButton from "./DeleteHomeworkButton.tsx";
import UpdateHomeworkButton from "./UpdateHomeworkButton.tsx";
import CardContainer from "../shared/CardContainer.tsx";

interface Props {
  homework: Homework;
}

const HomeworksCard = ({ homework }: Props) => {
  return (
    <CardContainer>
      <Stack spacing={4} align={"center"}>
        <TextModal header={"Homework Description"} text={homework.text} />
        <Text color={"gray.500"}>Due {homework.dueDate}</Text>
      </Stack>
      <HStack>
        <UpdateHomeworkButton homework={homework} />
        <DeleteHomeworkButton homework={homework} />
      </HStack>
    </CardContainer>
  );
};
export default HomeworksCard;

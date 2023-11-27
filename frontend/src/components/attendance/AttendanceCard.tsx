import { Attendance } from "../../entities/attendance/Attendance.ts";
import CardContainer from "../shared/CardContainer.tsx";
import { Heading, HStack, Stack, Text } from "@chakra-ui/react";
import EditButton from "../shared/EditButton.tsx";
import StringUtils from "../../utils/string-utils.ts";
import AttendanceFormatter from "../../utils/attendance-formatter.ts";
import DeleteAttendanceButton from "./DeleteAttendanceButton.tsx";
import UpdateAttendanceButton from "./UpdateAttendanceButton.tsx";

interface Props {
  attendance: Attendance;
}

const AttendanceCard = ({ attendance }: Props) => {
  return (
    <CardContainer>
      <Stack spacing={2} align={"center"} textAlign={"center"}>
        <Heading size={"lg"}>
          {StringUtils.getTitleString(attendance.type)}
        </Heading>
        <Heading size={"md"}>{attendance.studentName}</Heading>
        <Text>
          {AttendanceFormatter.getFormattedAttendancePeriod(attendance.period)}
        </Text>
        <Text color={"gray.500"}>Published {attendance.datePublished}</Text>
      </Stack>
      <HStack>
        <UpdateAttendanceButton attendance={attendance} />
        <DeleteAttendanceButton attendance={attendance} />
      </HStack>
    </CardContainer>
  );
};
export default AttendanceCard;

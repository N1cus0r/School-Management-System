import { ArcElement, Chart as ChartJS, Legend, Tooltip } from "chart.js";
import { Doughnut } from "react-chartjs-2";
import { Attendance } from "../../entities/attendance/Attendance.ts";
import { AttendanceType } from "../../entities/attendance/AttendanceType.ts";
import { Box, Heading, Text, VStack } from "@chakra-ui/react";

interface Props {
  attendancePages: Attendance[][];
}

ChartJS.register(ArcElement, Tooltip, Legend);

const AttendancesChart = ({ attendancePages }: Props) => {
  let numberOfAbsent = 0;
  let numberOfLate = 0;
  let numberOfMotivated = 0;

  for (let page of attendancePages) {
    for (let attendance of page) {
      switch (attendance.type) {
        case AttendanceType.ABSENT:
          numberOfAbsent++;
          break;
        case AttendanceType.LATE:
          numberOfLate++;
          break;
        case AttendanceType.MOTIVATED:
          numberOfMotivated++;
          break;
      }
    }
  }

  const data = {
    labels: ["Absent", "Late", "Motivated"],
    datasets: [
      {
        data: [numberOfAbsent, numberOfLate, numberOfMotivated],
        backgroundColor: ["#F56565", "#0BC5EA", "#48BB78"],
        borderColor: ["#F56565", "#0BC5EA", "#48BB78"],
        borderWidth: 1,
      },
    ],
  };

  return (
    <VStack spacing={4}>
      <Doughnut data={data} style={{ width: 450, height: 450 }} />
      <Box textAlign={"center"}>
        <Heading size={"lg"}>
          Total: {numberOfAbsent + numberOfLate + numberOfMotivated}
        </Heading>
        <Text color={"gray.500"}>(Scroll till the end to see full stats)</Text>
      </Box>
    </VStack>
  );
};
export default AttendancesChart;

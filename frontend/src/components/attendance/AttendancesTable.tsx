import {Table, TableContainer, Tbody, Td, Th, Thead, Tr,} from "@chakra-ui/react";
import {Attendance} from "../../entities/attendance/Attendance.ts";
import StringUtils from "../../utils/string-utils.ts";
import AttendanceFormatter from "../../utils/attendance-formatter.ts";
import React from "react";

interface Props {
  attendancePages: Attendance[][];
}

const AttendancesTable = ({ attendancePages }: Props) => {
  return (
    <TableContainer>
      <Table variant="simple">
        <Thead>
          <Tr>
            <Th>Course</Th>
            <Th>Teacher</Th>
            <Th>Date</Th>
            <Th>Period</Th>
            <Th>Type</Th>
          </Tr>
        </Thead>
        <Tbody>
          {attendancePages.map((page, index) => (
            <React.Fragment key={index}>
              {page.map((attendance) => (
                <Tr key={attendance.id}>
                  <Td>{attendance.courseName}</Td>
                  <Td>{attendance.courseTeacher}</Td>
                  <Td>{attendance.datePublished}</Td>
                  <Td>
                    {AttendanceFormatter.getFormattedAttendancePeriod(
                      attendance.period,
                    )}
                  </Td>
                  <Td>{StringUtils.getTitleString(attendance.type)}</Td>
                </Tr>
              ))}
            </React.Fragment>
          ))}
        </Tbody>
      </Table>
    </TableContainer>
  );
};
export default AttendancesTable;

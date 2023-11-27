import {
  Table,
  TableContainer,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
} from "@chakra-ui/react";
import TextModal from "../shared/TextModal.tsx";
import DueDateBadge from "./DueDateBadge.tsx";
import { Homework } from "../../entities/Homework.ts";
import React from "react";

interface Props {
  homeworkPages: Homework[][];
}

const HomeworksTable = ({ homeworkPages }: Props) => {
  return (
    <TableContainer>
      <Table variant="simple">
        <Thead>
          <Tr>
            <Th>Course</Th>
            <Th>Teacher</Th>
            <Th>Due Date</Th>
            <Th>Homework</Th>
          </Tr>
        </Thead>
        <Tbody>
          {homeworkPages.map((page, index) => (
            <React.Fragment key={index}>
              {page.map((homework) => (
                <Tr key={homework.id}>
                  <Td>{homework.courseName}</Td>
                  <Td>{homework.courseTeacher}</Td>
                  <Td>
                    <DueDateBadge dueDate={homework.dueDate} />
                  </Td>
                  <Td style={{ wordWrap: "break-word" }}>
                    <TextModal
                      header="Homework Description"
                      text={homework.text}
                    />
                  </Td>
                </Tr>
              ))}
            </React.Fragment>
          ))}
        </Tbody>
      </Table>
    </TableContainer>
  );
};
export default HomeworksTable;

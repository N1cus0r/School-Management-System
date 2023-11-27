import {Comment} from "../../entities/Comment.ts";
import {Table, TableContainer, Tbody, Td, Th, Thead, Tr,} from "@chakra-ui/react";
import TextModal from "../shared/TextModal.tsx";
import React from "react";

interface Props {
  commentPages: Comment[][];
}
const CommentsTable = ({ commentPages }: Props) => {
  return (
    <TableContainer>
      <Table variant="simple">
        <Thead>
          <Tr>
            <Th>Course</Th>
            <Th>Teacher</Th>
            <Th>Date</Th>
            <Th>Comment</Th>
          </Tr>
        </Thead>
        <Tbody>
          {commentPages.map((page, index) => (
            <React.Fragment key={index}>
              {page.map((comment) => (
                <Tr key={comment.id}>
                  <Td>{comment.courseName}</Td>
                  <Td>{comment.courseTeacher}</Td>
                  <Td>{comment.datePublished}</Td>
                  <Td style={{ wordWrap: "break-word" }}>
                    <TextModal
                      header="Comment Description"
                      text={comment.text}
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
export default CommentsTable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package vn.edu.fpt.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author PC
 */
public class ContractTest {
    
    public ContractTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getId method, of class Contract.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Contract instance = new Contract();
        long expResult = 0L;
        long result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class Contract.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        long id = 0L;
        Contract instance = new Contract();
        instance.setId(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContractCode method, of class Contract.
     */
    @Test
    public void testGetContractCode() {
        System.out.println("getContractCode");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getContractCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setContractCode method, of class Contract.
     */
    @Test
    public void testSetContractCode() {
        System.out.println("setContractCode");
        String contractCode = "";
        Contract instance = new Contract();
        instance.setContractCode(contractCode);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContractName method, of class Contract.
     */
    @Test
    public void testGetContractName() {
        System.out.println("getContractName");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getContractName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setContractName method, of class Contract.
     */
    @Test
    public void testSetContractName() {
        System.out.println("setContractName");
        String contractName = "";
        Contract instance = new Contract();
        instance.setContractName(contractName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnterpriseId method, of class Contract.
     */
    @Test
    public void testGetEnterpriseId() {
        System.out.println("getEnterpriseId");
        Contract instance = new Contract();
        long expResult = 0L;
        long result = instance.getEnterpriseId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEnterpriseId method, of class Contract.
     */
    @Test
    public void testSetEnterpriseId() {
        System.out.println("setEnterpriseId");
        long enterpriseId = 0L;
        Contract instance = new Contract();
        instance.setEnterpriseId(enterpriseId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCreatedById method, of class Contract.
     */
    @Test
    public void testGetCreatedById() {
        System.out.println("getCreatedById");
        Contract instance = new Contract();
        Long expResult = null;
        Long result = instance.getCreatedById();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCreatedById method, of class Contract.
     */
    @Test
    public void testSetCreatedById() {
        System.out.println("setCreatedById");
        Long createdById = null;
        Contract instance = new Contract();
        instance.setCreatedById(createdById);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStartDate method, of class Contract.
     */
    @Test
    public void testGetStartDate() {
        System.out.println("getStartDate");
        Contract instance = new Contract();
        Date expResult = null;
        Date result = instance.getStartDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStartDate method, of class Contract.
     */
    @Test
    public void testSetStartDate() {
        System.out.println("setStartDate");
        Date startDate = null;
        Contract instance = new Contract();
        instance.setStartDate(startDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEndDate method, of class Contract.
     */
    @Test
    public void testGetEndDate() {
        System.out.println("getEndDate");
        Contract instance = new Contract();
        Date expResult = null;
        Date result = instance.getEndDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEndDate method, of class Contract.
     */
    @Test
    public void testSetEndDate() {
        System.out.println("setEndDate");
        Date endDate = null;
        Contract instance = new Contract();
        instance.setEndDate(endDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSignedDate method, of class Contract.
     */
    @Test
    public void testGetSignedDate() {
        System.out.println("getSignedDate");
        Contract instance = new Contract();
        Date expResult = null;
        Date result = instance.getSignedDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSignedDate method, of class Contract.
     */
    @Test
    public void testSetSignedDate() {
        System.out.println("setSignedDate");
        Date signedDate = null;
        Contract instance = new Contract();
        instance.setSignedDate(signedDate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatusId method, of class Contract.
     */
    @Test
    public void testGetStatusId() {
        System.out.println("getStatusId");
        Contract instance = new Contract();
        int expResult = 0;
        int result = instance.getStatusId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStatusId method, of class Contract.
     */
    @Test
    public void testSetStatusId() {
        System.out.println("setStatusId");
        int statusId = 0;
        Contract instance = new Contract();
        instance.setStatusId(statusId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatusName method, of class Contract.
     */
    @Test
    public void testGetStatusName() {
        System.out.println("getStatusName");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getStatusName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStatusName method, of class Contract.
     */
    @Test
    public void testSetStatusName() {
        System.out.println("setStatusName");
        String statusName = "";
        Contract instance = new Contract();
        instance.setStatusName(statusName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalValue method, of class Contract.
     */
    @Test
    public void testGetTotalValue() {
        System.out.println("getTotalValue");
        Contract instance = new Contract();
        BigDecimal expResult = null;
        BigDecimal result = instance.getTotalValue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalValue method, of class Contract.
     */
    @Test
    public void testSetTotalValue() {
        System.out.println("setTotalValue");
        BigDecimal totalValue = null;
        Contract instance = new Contract();
        instance.setTotalValue(totalValue);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNotes method, of class Contract.
     */
    @Test
    public void testGetNotes() {
        System.out.println("getNotes");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getNotes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNotes method, of class Contract.
     */
    @Test
    public void testSetNotes() {
        System.out.println("setNotes");
        String notes = "";
        Contract instance = new Contract();
        instance.setNotes(notes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileUrl method, of class Contract.
     */
    @Test
    public void testGetFileUrl() {
        System.out.println("getFileUrl");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getFileUrl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFileUrl method, of class Contract.
     */
    @Test
    public void testSetFileUrl() {
        System.out.println("setFileUrl");
        String fileUrl = "";
        Contract instance = new Contract();
        instance.setFileUrl(fileUrl);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIsDeleted method, of class Contract.
     */
    @Test
    public void testIsIsDeleted() {
        System.out.println("isIsDeleted");
        Contract instance = new Contract();
        boolean expResult = false;
        boolean result = instance.isIsDeleted();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIsDeleted method, of class Contract.
     */
    @Test
    public void testSetIsDeleted() {
        System.out.println("setIsDeleted");
        boolean isDeleted = false;
        Contract instance = new Contract();
        instance.setIsDeleted(isDeleted);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCreatedAt method, of class Contract.
     */
    @Test
    public void testGetCreatedAt() {
        System.out.println("getCreatedAt");
        Contract instance = new Contract();
        Timestamp expResult = null;
        Timestamp result = instance.getCreatedAt();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCreatedAt method, of class Contract.
     */
    @Test
    public void testSetCreatedAt() {
        System.out.println("setCreatedAt");
        Timestamp createdAt = null;
        Contract instance = new Contract();
        instance.setCreatedAt(createdAt);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUpdatedAt method, of class Contract.
     */
    @Test
    public void testGetUpdatedAt() {
        System.out.println("getUpdatedAt");
        Contract instance = new Contract();
        Timestamp expResult = null;
        Timestamp result = instance.getUpdatedAt();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUpdatedAt method, of class Contract.
     */
    @Test
    public void testSetUpdatedAt() {
        System.out.println("setUpdatedAt");
        Timestamp updatedAt = null;
        Contract instance = new Contract();
        instance.setUpdatedAt(updatedAt);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnterpriseName method, of class Contract.
     */
    @Test
    public void testGetEnterpriseName() {
        System.out.println("getEnterpriseName");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getEnterpriseName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEnterpriseName method, of class Contract.
     */
    @Test
    public void testSetEnterpriseName() {
        System.out.println("setEnterpriseName");
        String enterpriseName = "";
        Contract instance = new Contract();
        instance.setEnterpriseName(enterpriseName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCreatedByName method, of class Contract.
     */
    @Test
    public void testGetCreatedByName() {
        System.out.println("getCreatedByName");
        Contract instance = new Contract();
        String expResult = "";
        String result = instance.getCreatedByName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCreatedByName method, of class Contract.
     */
    @Test
    public void testSetCreatedByName() {
        System.out.println("setCreatedByName");
        String createdByName = "";
        Contract instance = new Contract();
        instance.setCreatedByName(createdByName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
